package org.truenewx.tnxjeex.cas.server.ticket;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.cas.core.validation.SimpleAssertion;
import org.truenewx.tnxjeex.cas.server.entity.AppTicket;
import org.truenewx.tnxjeex.cas.server.entity.TicketGrantingTicket;
import org.truenewx.tnxjeex.cas.server.repo.AppTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.MemoryAppTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.MemoryTicketGrantingTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.TicketGrantingTicketRepo;
import org.truenewx.tnxjeex.cas.server.security.authentication.CasServerUserSpecificDetailsScopeSwitch;

/**
 * CAS票据管理器实现
 */
@Service
public class CasTicketManagerImpl implements CasTicketManager {
    @Autowired
    private ServerProperties serverProperties;
    @Autowired(required = false) // 没有登录范围区别的系统没有范围切换器实现
    private CasServerUserSpecificDetailsScopeSwitch userSpecificDetailsScopeSwitch;
    private TicketGrantingTicketRepo ticketGrantingTicketRepo = new MemoryTicketGrantingTicketRepo();
    private AppTicketRepo appTicketRepo = new MemoryAppTicketRepo();

    @Autowired(required = false)
    public void setTicketGrantingTicketRepo(TicketGrantingTicketRepo ticketGrantingTicketRepo) {
        this.ticketGrantingTicketRepo = ticketGrantingTicketRepo;
    }

    @Autowired(required = false)
    public void setAppTicketRepo(AppTicketRepo appTicketRepo) {
        this.appTicketRepo = appTicketRepo;
    }

    @Override
    @WriteTransactional
    public void createTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String ticketGrantingTicketId = TICKET_GRANTING_TICKET_PREFIX + EncryptUtil
                .encryptByMd5(session.getId() + System.currentTimeMillis());
        TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicket(ticketGrantingTicketId);
        ticketGrantingTicket.setUserDetails(SecurityUtil.getAuthorizedUserDetails());
        Date createTime = new Date();
        ticketGrantingTicket.setCreateTime(createTime);
        Duration timeout = this.serverProperties.getServlet().getSession().getTimeout();
        Date expiredTime = new Date(createTime.getTime() + timeout.toMillis());
        ticketGrantingTicket.setExpiredTime(expiredTime);
        this.ticketGrantingTicketRepo.save(ticketGrantingTicket);

        // 按照CAS规范将TGT写入Cookie
        int cookieMaxAge = (int) timeout.toSeconds();
        WebUtil.addCookie(request, response, TGT_NAME, ticketGrantingTicketId, cookieMaxAge);

        // Cookie中的TGT需要到下一个请求时才能获取，缓存TGT到当前会话，以便当前请求的后续处理获取TGT
        session.setAttribute(TGT_NAME, ticketGrantingTicketId);
    }

    /**
     * 读取已有的票据授权票据id，如果没有则返回null
     *
     * @param request 请求
     * @return 票据授权票据id
     */
    private String readTicketGrantingTicketId(HttpServletRequest request) {
        // 优先从当前会话缓存中获取TGT
        String ticketGrantingTicketId = (String) request.getSession().getAttribute(TGT_NAME);
        if (ticketGrantingTicketId == null) {
            ticketGrantingTicketId = WebUtil.getCookieValue(request, TGT_NAME);
        }
        return ticketGrantingTicketId;
    }

    /**
     * 查找有效的票据授权票据实体，如果没有或已过期则返回null
     *
     * @param request 请求
     * @return 票据授权票据实体
     */
    private TicketGrantingTicket findValidTicketGrantingTicket(HttpServletRequest request) {
        String ticketGrantingTicketId = readTicketGrantingTicketId(request);
        if (ticketGrantingTicketId != null) {
            TicketGrantingTicket ticketGrantingTicket = this.ticketGrantingTicketRepo.findById(ticketGrantingTicketId)
                    .orElse(null);
            if (ticketGrantingTicket != null) {
                if (ticketGrantingTicket.getExpiredTime().getTime() > System.currentTimeMillis()) {
                    return ticketGrantingTicket;
                } else { // 如果已过期则删除，以尽量减少垃圾数据
                    this.ticketGrantingTicketRepo.delete(ticketGrantingTicket);
                }
            }
        }
        return null;
    }

    @Override
    @WriteTransactional
    public boolean checkTicketGrantingTicket(HttpServletRequest request) {
        return findValidTicketGrantingTicket(request) != null;
    }

    // 用户登录或登出CAS服务器成功后调用，以获取目标应用的票据
    @Override
    @WriteTransactional
    public String getAppTicketId(HttpServletRequest request, String app, String scope) {
        TicketGrantingTicket ticketGrantingTicket = findValidTicketGrantingTicket(request);
        if (ticketGrantingTicket != null) {
            String ticketGrantingTicketId = ticketGrantingTicket.getId();
            AppTicket appTicket = this.appTicketRepo
                    .findByTicketGrantingTicketIdAndApp(ticketGrantingTicketId, app);
            if (appTicket == null) { // 不存在则创建新的
                // 创建新的服务票据前，先进行可能的范围切换动作
                if (this.userSpecificDetailsScopeSwitch != null) {
                    UserSpecificDetails<?> userDetails = ticketGrantingTicket.getUserDetails();
                    if (this.userSpecificDetailsScopeSwitch.switchScope(userDetails, scope)) {
                        this.ticketGrantingTicketRepo.save(ticketGrantingTicket);
                    }
                }

                Date now = new Date();
                String text = ticketGrantingTicketId + Strings.MINUS + app + Strings.MINUS + now.getTime();
                String appTicketId = SERVICE_TICKET_PREFIX + EncryptUtil.encryptByMd5(text);
                appTicket = new AppTicket(appTicketId);
                appTicket.setTicketGrantingTicket(ticketGrantingTicket);
                appTicket.setApp(app);
                appTicket.setCreateTime(now);
                // 所属票据授权票据的过期时间即为服务票据的过期时间
                appTicket.setExpiredTime(ticketGrantingTicket.getExpiredTime());
                this.appTicketRepo.save(appTicket);
            }
            return appTicket.getId();
        }
        return null;
    }

    @Override
    public Collection<AppTicket> deleteTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response) {
        TicketGrantingTicket ticketGrantingTicket = findValidTicketGrantingTicket(request);
        if (ticketGrantingTicket != null) {
            Collection<AppTicket> appTickets = this.appTicketRepo
                    .deleteByTicketGrantingTicketId(ticketGrantingTicket.getId());
            this.ticketGrantingTicketRepo.delete(ticketGrantingTicket);
            // 按照CAS规范将TGT从Cookie移除
            WebUtil.removeCookie(request, response, TGT_NAME);
            return appTickets;
        }
        return Collections.emptyList();
    }

    // 用户访问业务服务，由业务服务校验票据有效性时调用
    @Override
    public Assertion validateAppTicket(String app, String appTicketId) {
        AppTicket appTicket = this.appTicketRepo.findById(appTicketId).orElse(null);
        if (appTicket == null || !appTicket.getApp().equals(app)) {
            return null;
        }
        UserSpecificDetails<?> userDetails = appTicket.getTicketGrantingTicket().getUserDetails();
        SimpleAssertion assertion = new SimpleAssertion();
        assertion.setUserDetails(userDetails);
        assertion.setValidFromDate(appTicket.getCreateTime());
        assertion.setValidUntilDate(appTicket.getExpiredTime());
        assertion.setAuthenticationDate(appTicket.getCreateTime());
        return assertion;
    }

}

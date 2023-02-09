package org.truenewx.tnxjeex.cas.server.ticket;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.cas.core.CasConstants;
import org.truenewx.tnxjeex.cas.core.validation.SimpleAssertion;
import org.truenewx.tnxjeex.cas.server.authentication.CasServerScopeResolver;
import org.truenewx.tnxjeex.cas.server.entity.AppTicket;
import org.truenewx.tnxjeex.cas.server.entity.TicketGrantingTicket;
import org.truenewx.tnxjeex.cas.server.repo.AppTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.MemoryAppTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.MemoryTicketGrantingTicketRepo;
import org.truenewx.tnxjeex.cas.server.repo.TicketGrantingTicketRepo;
import org.truenewx.tnxjeex.cas.server.service.CasServerExceptionCodes;

/**
 * CAS票据管理器实现
 */
@Service
public class CasTicketManagerImpl implements CasTicketManager {
    @Autowired
    private ServerProperties serverProperties;
    @Autowired(required = false) // 没有登录范围区别的系统没有范围解决器实现
    private CasServerScopeResolver scopeResolver;
    private TicketGrantingTicketRepo ticketGrantingTicketRepo = new MemoryTicketGrantingTicketRepo();
    private AppTicketRepo appTicketRepo = new MemoryAppTicketRepo();
    @Autowired(required = false) // 一般情况下无需进行转换，没有该转换器实现
    private CasUserDetailsConverter userDetailsConverter;

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
        WebUtil.setSessionCookie(request, response, CasConstants.COOKIE_TGT, ticketGrantingTicketId);

        // Cookie中的TGT需要到下一个请求时才能获取，缓存TGT到当前会话，以便当前请求的后续处理获取TGT
        session.setAttribute(CasConstants.COOKIE_TGT, ticketGrantingTicketId);
    }

    /**
     * 读取已有的票据授权票据id，如果没有则返回null
     *
     * @param request 请求
     * @return 票据授权票据id
     */
    private String readTicketGrantingTicketId(HttpServletRequest request) {
        // 优先从当前会话缓存中获取TGT
        String ticketGrantingTicketId = (String) request.getSession().getAttribute(CasConstants.COOKIE_TGT);
        if (ticketGrantingTicketId == null) {
            ticketGrantingTicketId = WebUtil.getCookieValue(request, CasConstants.COOKIE_TGT);
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
        LogUtil.debug(getClass(), "\n====== {}\ntgtId = {}", request.getRequestURL(), ticketGrantingTicketId);
        if (ticketGrantingTicketId != null) {
            TicketGrantingTicket ticketGrantingTicket = this.ticketGrantingTicketRepo.findById(ticketGrantingTicketId)
                    .orElse(null);
            if (ticketGrantingTicket != null) {
                if (ticketGrantingTicket.getExpiredTime().getTime() > System.currentTimeMillis()) {
                    LogUtil.debug(getClass(), "====== valid");
                    return ticketGrantingTicket;
                } else { // 如果已过期则删除，以尽量减少垃圾数据
                    LogUtil.debug(getClass(), "====== expired");
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
            AppTicket appTicket = this.appTicketRepo.findByTicketGrantingTicketIdAndApp(ticketGrantingTicketId, app);
            if (appTicket == null) { // 不存在则创建新的
                // 创建新的服务票据前，先进行可能的范围切换动作
                if (this.scopeResolver != null) {
                    UserSpecificDetails<?> userDetails = ticketGrantingTicket.getUserDetails();
                    if (this.scopeResolver.applyScope(userDetails, scope)) {
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
    public UserSpecificDetails<?> getUserDetailsInTicketGrantingTicket(HttpServletRequest request) {
        TicketGrantingTicket ticket = findValidTicketGrantingTicket(request);
        if (ticket != null) {
            return ticket.getUserDetails();
        }
        return null;
    }

    @Override
    public List<AppTicket> deleteAppTickets(HttpServletRequest request, String excludedApp) {
        String ticketGrantingTicketId = readTicketGrantingTicketId(request);
        if (ticketGrantingTicketId != null) {
            return this.appTicketRepo.deleteByTicketGrantingTicketIdAndAppNot(ticketGrantingTicketId, excludedApp);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AppTicket> deleteTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response) {
        TicketGrantingTicket ticketGrantingTicket = findValidTicketGrantingTicket(request);
        if (ticketGrantingTicket != null) {
            List<AppTicket> appTickets = this.appTicketRepo.deleteByTicketGrantingTicketIdAndAppNot(
                    ticketGrantingTicket.getId(), null);
            this.ticketGrantingTicketRepo.delete(ticketGrantingTicket);
            // 按照CAS规范将TGT从Cookie移除
            WebUtil.removeCookie(request, response, CasConstants.COOKIE_TGT);
            return appTickets;
        }
        return Collections.emptyList();
    }

    // 用户访问业务服务，由业务服务校验票据有效性时调用
    @Override
    public Assertion validateAppTicket(String app, String appTicketId) {
        AppTicket appTicket = this.appTicketRepo.findById(appTicketId).orElse(null);
        if (appTicket == null || !appTicket.getApp().equals(app)) {
            throw new BusinessException(CasServerExceptionCodes.INVALID_APP_TICKET);
        }
        UserSpecificDetails<?> userDetails = appTicket.getTicketGrantingTicket().getUserDetails();
        if (this.userDetailsConverter != null) {
            UserSpecificDetails<?> newUserDetails = this.userDetailsConverter.convert(app, userDetails);
            if (newUserDetails != null) {
                userDetails = newUserDetails;
            }
        }
        SimpleAssertion assertion = new SimpleAssertion();
        assertion.setUserDetails(userDetails);
        assertion.setValidFromDate(appTicket.getCreateTime());
        assertion.setValidUntilDate(appTicket.getExpiredTime());
        assertion.setAuthenticationDate(new Date());
        return assertion;
    }

}

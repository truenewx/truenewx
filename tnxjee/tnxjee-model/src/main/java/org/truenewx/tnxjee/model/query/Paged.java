package org.truenewx.tnxjee.model.query;

/**
 * 分页结果
 *
 * @author jianglei
 */
public class Paged extends Pagination {

    private static final long serialVersionUID = 2748051722289562458L;

    private Long total;
    private boolean morePage;

    protected Paged() {
    }

    public Paged(int pageSize, int pageNo, long total) {
        super(pageSize, pageNo);
        this.total = total;
        this.morePage = ((long) pageSize * pageNo) < total;
    }

    public Paged(int pageSize, int pageNo, boolean morePage) {
        super(pageSize, pageNo);
        this.morePage = morePage;
    }

    public static Paged of(Pagination pagination, long total) {
        Paged paged = new Paged(pagination.getPageSize(), pagination.getPageNo(), total);
        paged.setOrders(pagination.getOrders());
        return paged;
    }

    public static Paged of(Pagination pagination, boolean morePage) {
        Paged paged = new Paged(pagination.getPageSize(), pagination.getPageNo(), morePage);
        paged.setOrders(pagination.getOrders());
        return paged;
    }

    public Long getTotal() {
        return this.total;
    }

    public boolean isMorePage() {
        return this.morePage;
    }

    //////

    public boolean isCountable() {
        return this.total != null && this.total >= 0;
    }

    public int getPageCount() {
        if (isPageable()) {
            if (isCountable()) {
                int pageSize = getPageSize();
                int pageCount = (int) (this.total / pageSize);
                if (this.total % pageSize != 0) {
                    pageCount++;
                }
                return pageCount;
            } else if (!this.morePage) { // 无总数但没有更多页时，当前页码即为总页数
                return getPageNo();
            }
        }
        return 0;
    }

    public int getPreviousPage() {
        int pageNo = getPageNo();
        return pageNo <= 1 ? 1 : (pageNo - 1);
    }

    public int getNextPage() {
        int pageNo = getPageNo();
        int pageCount = getPageCount();
        return (this.total >= 0 && pageNo >= pageCount) ? pageCount : (pageNo + 1);
    }

}

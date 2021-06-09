package org.truenewx.tnxjeex.office.excel;

import org.apache.poi.ss.util.CellAddress;
import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * Excel单元格异常
 *
 * @author jianglei
 */
public class ExcelCellException extends BusinessException {

    private static final long serialVersionUID = -3952816185244835480L;

    private CellAddress address;

    public ExcelCellException(CellAddress address, String code, Object... args) {
        super(code, args);
        this.address = address;
    }

    public CellAddress getAddress() {
        return this.address;
    }

    public void clearAddress() {
        this.address = null;
    }

}

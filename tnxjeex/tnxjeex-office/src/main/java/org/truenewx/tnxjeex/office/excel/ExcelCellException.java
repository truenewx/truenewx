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
        super(code, mergeLocationToArgs(address, args));
        this.address = address;
    }

    private static Object[] mergeLocationToArgs(CellAddress address, Object... args) {
        String location = address.formatAsString();
        if (args.length == 0) {
            return new Object[]{ location };
        } else {
            Object[] mergedArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, mergedArgs, 1, args.length);
            mergedArgs[0] = location;
            return mergedArgs;
        }
    }

    public CellAddress getAddress() {
        return this.address;
    }
}

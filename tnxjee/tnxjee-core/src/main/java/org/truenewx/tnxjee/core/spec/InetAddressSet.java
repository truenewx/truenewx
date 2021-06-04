package org.truenewx.tnxjee.core.spec;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * IP地址集合，可同时包含IPv4和IPv6
 *
 * @author jianglei
 * 
 */
public class InetAddressSet {
    private List<InetAddressRange<?>> ranges = new ArrayList<>();

    public void add(InetAddress address) {
        // 遍历已有的地址段，尝试加入其中
        for (InetAddressRange<?> range : this.ranges) {
            if (range.add(address)) { // 如果加入某个地址段成功，则返回
                return;
            }
        }
        // 不能加入任何已有的地址段，则生成新的地址段加入地址段清单中
        this.ranges.add(new InetAddressRange<>(address));
    }

    public void add(InetAddressRange<?> range) {
        // 遍历已有的地址段，尝试加入其中
        for (InetAddressRange<?> r : this.ranges) {
            if (r.add(range)) { // 如果加入某个地址段成功，则返回
                return;
            }
        }
        // 不能加入任何已有的地址段，则作为新的地址段加入地址段清单中
        this.ranges.add(range);
    }

    public boolean contains(InetAddress address) {
        for (InetAddressRange<?> range : this.ranges) {
            if (range.contains(address)) {
                return true;
            }
        }
        return false;
    }

    public Iterable<InetAddressRange<?>> getRanges() {
        return this.ranges;
    }

    @Override
    public String toString() {
        return StringUtils.join(this.ranges, ", ");
    }

}

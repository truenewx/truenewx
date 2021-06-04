package org.truenewx.tnxjee.repo.jpa.hibernate;

import org.hibernate.boot.Metadata;

/**
 * Hibernate元数据提供者
 */
public interface MetadataProvider {

    Metadata getMetadata();

}

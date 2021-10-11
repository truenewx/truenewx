package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.service.impl.unity.AbstractService;

import ${entityClassName};
import ${repoClassName};

/**
 * @author tnxjee-code-generator
 */
@Service
public class ${serviceClassSimpleName} extends AbstractService<${entityClassSimpleName}> implements ${serviceInterfaceSimpleName} {

    @Autowired
    private ${repoClassSimpleName} repo;

}

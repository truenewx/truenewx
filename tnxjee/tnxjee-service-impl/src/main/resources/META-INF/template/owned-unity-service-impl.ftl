package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.service.impl.unity.AbstractOwnedUnityService;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};
import ${repoClassName};

/**
 * @author tnxjee-code-generator
 */
@Service
public class ${serviceClassSimpleName} extends AbstractOwnedUnityService<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}> implements ${serviceInterfaceSimpleName} {

    @Autowired
    private ${repoClassSimpleName} repox;

}

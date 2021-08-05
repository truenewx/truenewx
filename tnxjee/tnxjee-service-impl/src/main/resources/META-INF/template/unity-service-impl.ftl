package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.service.impl.unity.AbstractUnityService;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};
import ${repoClassName};

/**
* @author tnxjee-code-generator
*/
@Service
public class ${serviceClassSimpleName} extends AbstractUnityService<${entityClassSimpleName}, ${keyClassSimpleName}> implements ${serviceInterfaceSimpleName} {

@Autowired
private ${repoClassSimpleName} repo;

}

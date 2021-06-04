package ${packageName};

import org.truenewx.tnxjee.service.unity.SimpleUnityService;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends SimpleUnityService<${entityClassSimpleName}, ${keyClassSimpleName}> {
}

package ${packageName};

import org.truenewx.tnxjee.service.unity.CommandUnityService;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends CommandUnityService<${entityClassSimpleName}, ${keyClassSimpleName}> {
}

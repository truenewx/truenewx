package ${packageName};

import org.truenewx.tnxjee.service.unity.CommandOwnedUnityService;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends CommandOwnedUnityService<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}> {
}

package ${packageName};

import org.truenewx.tnxjee.service.unity.SimpleOwnedUnityService;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends SimpleOwnedUnityService<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}> {
}

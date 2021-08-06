package ${packageName};

import org.truenewx.tnxjee.repo.OwnedUnityRepox;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends OwnedUnityRepox<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}>{
}

package ${packageName};

import org.truenewx.tnxjee.repo.OwnedUnityRepo;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends OwnedUnityRepo<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}>{
}

package ${packageName};

import org.truenewx.tnxjee.repo.UnityRepox;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends UnityRepox<${entityClassSimpleName}, ${keyClassSimpleName}> {
}

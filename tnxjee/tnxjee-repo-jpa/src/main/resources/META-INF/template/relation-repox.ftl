package ${packageName};

import org.truenewx.tnxjee.repo.RelationRepox;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends RelationRepox<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}>{
}

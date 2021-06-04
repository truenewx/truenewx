package ${packageName};

import org.truenewx.tnxjee.service.relation.CommandRelationService;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends CommandRelationService<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}> {
}

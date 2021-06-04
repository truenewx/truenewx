package ${packageName};

import org.truenewx.tnxjee.service.relation.SimpleRelationService;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${serviceClassSimpleName} extends SimpleRelationService<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}> {
}

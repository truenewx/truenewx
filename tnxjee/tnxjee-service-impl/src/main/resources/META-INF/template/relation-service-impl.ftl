package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.service.impl.relation.AbstractRelationService;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};
import ${repoClassName};

/**
* @author tnxjee-code-generator
*/
@Service
public class ${serviceClassSimpleName} extends AbstractRelationService<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}> implements ${serviceInterfaceSimpleName} {

@Autowired
private ${repoClassSimpleName} repo;

}

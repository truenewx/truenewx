package ${packageName};

import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.repo.jpa.support.JpaOwnedUnityRepoxSupport;
<#if keyClassName??>import ${keyClassName};</#if>
<#if ownerClassName??>import ${ownerClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
@Repository
public class ${repoClassSimpleName} extends JpaOwnedUnityRepoxSupport<${entityClassSimpleName}, ${keyClassSimpleName}, ${ownerClassSimpleName}> implements ${repoxClassSimpleName} {
}

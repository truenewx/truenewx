package ${packageName};

import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.repo.jpa.support.JpaUnityRepoxSupport;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
@Repository
public class ${repoClassSimpleName} extends JpaUnityRepoxSupport<${entityClassSimpleName}, ${keyClassSimpleName}> implements ${repoxClassSimpleName} {
}

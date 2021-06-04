package ${packageName};

import org.springframework.data.jpa.repository.JpaRepository;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends JpaRepository<${entityClassSimpleName}, ${keyClassSimpleName}><#if repoxClassSimpleName??>, ${repoxClassSimpleName}</#if> {
}

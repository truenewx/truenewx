package ${packageName};

import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.repo.jpa.support.JpaRelationRepoxSupport;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
@Repository
public class ${repoClassSimpleName} extends JpaRelationRepoxSupport<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}> implements ${repoxClassSimpleName} {

    @Override
    protected Binate<String, String> getIdProperty() {
        return new Binary<>("${leftKeyPropertyName}", "${rightKeyPropertyName}");
    }

}

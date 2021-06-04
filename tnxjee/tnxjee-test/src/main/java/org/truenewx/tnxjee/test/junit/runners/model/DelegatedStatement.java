package org.truenewx.tnxjee.test.junit.runners.model;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.truenewx.tnxjee.test.junit.rules.StatementProcedure;

/**
 * 委派给语句过程去执行的语句
 *
 * @author jianglei
 */
public class DelegatedStatement extends Statement {
    private Statement base;
    private Description description;
    private StatementProcedure procedure;

    public DelegatedStatement(Statement base, Description description,
            StatementProcedure procedure) {
        this.base = base;
        this.description = description;
        this.procedure = procedure;
    }

    @Override
    public void evaluate() throws Throwable {
        this.procedure.evaluate(this.base, this.description);
    }

}

package org.test.streaming;

import java.io.OutputStream;

public interface MovieRetrievalPlanInterpreter {

	public void interpret(MovieRetrievalPlan plan, OutputStream out);

}

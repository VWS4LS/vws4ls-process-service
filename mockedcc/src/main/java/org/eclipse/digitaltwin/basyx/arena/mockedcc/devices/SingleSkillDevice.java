package org.eclipse.digitaltwin.basyx.arena.mockedcc.devices;

import org.eclipse.digitaltwin.basyx.arena.mockedcc.controllers.OperationChain.OperationFunctionProvider;

public abstract class SingleSkillDevice implements OperationFunctionProvider {

    abstract public String getDeviceName();
}

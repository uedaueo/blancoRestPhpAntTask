/*
 * blanco Framework
 * Copyright (C) 2004-2005 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.sample.task;

import java.io.IOException;

import blanco.sample.task.valueobject.BlancoAntTaskSampleProcessInput;

public class BlancoAntTaskSampleProcessImpl implements
        BlancoAntTaskSampleProcess {

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoAntTaskSampleProcessInput input)
            throws IOException, IllegalArgumentException {
        System.out.println(input.getAttr1());

        return BlancoAntTaskSampleBatchProcess.END_SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}

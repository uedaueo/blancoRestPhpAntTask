/*
 * blanco Framework
 * Copyright (C) 2004-2005 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.restphp.task;

import java.io.IOException;

import blanco.restphp.task.valueobject.BlancoRestPhpProcessInput;

public class BlancoRestPhpProcessImpl implements
        BlancoRestPhpProcess {

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoRestPhpProcessInput input)
            throws IOException, IllegalArgumentException {
        System.out.println(input.getMetadir());

        return BlancoRestPhpBatchProcess.END_SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}

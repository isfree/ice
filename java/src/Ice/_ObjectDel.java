// **********************************************************************
//
// Copyright (c) 2001
// MutableRealms, Inc.
// Huntsville, AL, USA
//
// All Rights Reserved
//
// **********************************************************************

package Ice;

public interface _ObjectDel
{
    boolean ice_isA(String __id, java.util.HashMap __context)
        throws LocationForward, IceInternal.NonRepeatable;

    void ice_ping(java.util.HashMap __context)
        throws LocationForward, IceInternal.NonRepeatable;

    byte[] ice_invoke(String operation, boolean nonmutating, byte[] inParams,
                      java.util.HashMap context)
        throws LocationForward, IceInternal.NonRepeatable;

    void ice_flush();
}

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

class CommunicatorI implements Communicator
{
    public synchronized void
    destroy()
    {
        if (_instance != null)
        {
            _instance.destroy();
            _instance = null;
        }
    }

    public void
    shutdown()
    {
        //
        // No mutex locking here!
        //
        _threadPool.initiateServerShutdown();
    }

    public void
    waitForShutdown()
    {
        //
        // No mutex locking here, otherwise the communicator is blocked
        // while waiting for shutdown.
        //
        _threadPool.waitUntilServerFinished();
    }

    public synchronized Ice.ObjectPrx
    stringToProxy(String s)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.proxyFactory().stringToProxy(s);
    }

    public synchronized String
    proxyToString(Ice.ObjectPrx proxy)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.proxyFactory().proxyToString(proxy);
    }

    public synchronized ObjectAdapter
    createObjectAdapter(String name)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        String endpts = _instance.properties().getProperty(
            "Ice.Adapter." + name + ".Endpoints");
        return createObjectAdapterWithEndpoints(name, endpts);
    }

    public synchronized ObjectAdapter
    createObjectAdapterWithEndpoints(String name, String endpts)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        /* TODO: Server
        return _instance.objectAdapterFactory().createObjectAdapter(
            name, endpts);
        */
        return null;
    }

    public synchronized void
    addObjectFactory(ObjectFactory factory, String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        _instance.servantFactoryManager().add(factory, id);
    }

    public synchronized void
    removeObjectFactory(String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        _instance.servantFactoryManager().remove(id);
    }

    public synchronized ObjectFactory
    findObjectFactory(String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.servantFactoryManager().find(id);
    }

    public synchronized void
    addUserExceptionFactory(UserExceptionFactory factory, String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        _instance.userExceptionFactoryManager().add(factory, id);
    }

    public synchronized void
    removeUserExceptionFactory(String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        _instance.userExceptionFactoryManager().remove(id);
    }

    public synchronized UserExceptionFactory
    findUserExceptionFactory(String id)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.userExceptionFactoryManager().find(id);
    }

    public synchronized Properties
    getProperties()
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.properties();
    }

    public synchronized Logger
    getLogger()
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        return _instance.logger();
    }

    public synchronized void
    setLogger(Logger logger)
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }
        _instance.logger(logger);
    }

    public Stream
    createStream()
    {
        if (_instance == null)
        {
            throw new CommunicatorDestroyedException();
        }

        return null;
    }

    CommunicatorI(Properties properties)
    {
        _instance = new IceInternal.Instance(this, properties);
        _threadPool = _instance.threadPool();
    }

    protected void
    finalize()
        throws Throwable
    {
        if (_instance != null)
        {
            _instance.logger().warning("communicator has not been destroyed");
        }

        super.finalize();
    }

    private IceInternal.Instance _instance;
    private IceInternal.ThreadPool _threadPool;
}

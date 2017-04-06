package com.warehouse.data.nio;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 11:50
 **/
public class NIOReactorPool {

    private final NIOReactor[] nioReactors;
    private volatile int nextReactor;

    public NIOReactorPool(String name, int poolSize) throws IOException {
        nioReactors = new NIOReactor[poolSize];
        for (int i = 0; i < poolSize; i++) {
            NIOReactor nioReactor = new NIOReactor(name + "-" + i);
            nioReactors[i] = nioReactor;
            nioReactor.startup();
        }
    }


    public NIOReactor getNextReactor() {
        int i = ++nextReactor;
        if (i > nioReactors.length) {
            i = nextReactor = 0;
        }
        return nioReactors[i];
    }
}

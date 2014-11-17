package com.mucommander.share;

import com.mucommander.share.impl.ImgurProvider;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mathias
 */
public class ShareManager {

    private final static Set<ShareProvider> providers = new HashSet();

    static {
        providers.add(new ImgurProvider());
    }

    public static Set<ShareProvider> getProviders() {

        return providers;
    }

    public void registerProvider(ShareProvider provider) {
        providers.add(provider);
    }

}

package com.primefaces.ext.base.web;

import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;

/**
 *
 * @author Jason
 * @date 2015-7-24
 */
public class FaceletsResourcesResolver extends ResourceHandlerWrapper {

    private final ResourceHandler wrapped;
//	private File externalResourceFolder;

    public FaceletsResourcesResolver(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public ViewResource createViewResource(FacesContext context, String path) {
        ViewResource resource = super.createViewResource(context, path);
        if (resource == null) {
            path = path.replace(context.getExternalContext().getRequestContextPath(), "");
            resource = super.createViewResource(context, path);
            /* find resources
			if (resource == null) {
				if (externalResourceFolder == null) {
					String appPath = context.getExternalContext().getRealPath("/");
					externalResourceFolder = new File(appPath);
				}
				final File externalResource = new File(externalResourceFolder, path);
				if (externalResource.exists()) {
					resource = new ViewResource() {
						@Override
						public URL getURL() {
							try {
								return externalResource.toURI().toURL();
							} catch (MalformedURLException e) {
								throw new FacesException(e);
							}
						}
					};
				}
			}
             */
        }
        return resource;
    }

}

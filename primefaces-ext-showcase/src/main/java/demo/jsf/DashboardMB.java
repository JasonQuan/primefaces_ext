package demo.jsf;

import com.primefaces.ext.base.web.bean.BaseDashboardMB;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Jason
 */
@ManagedBean
@ViewScoped
public class DashboardMB extends BaseDashboardMB {

    public void openSource() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("maximizable", true);
        options.put("minimizable", true);
        options.put("contentHeight", 500);
        options.put("contentWidth", 1024);
        RequestContext.getCurrentInstance().openDialog("dashboard_sources", options, null);
    }
}

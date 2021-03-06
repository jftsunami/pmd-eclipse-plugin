
package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.EditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.SizeChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.BooleanEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.CharacterEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.DoubleEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.EnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.FileEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.FloatEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.IntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiEnumerationEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiIntegerEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiMethodEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiStringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.MultiTypeEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.StringEditorFactory;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.TypeEditorFactory;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 * @author Brian Remedios
 */
public class PerRulePropertyPanelManager extends AbstractRulePanelManager implements SizeChangeListener {

    private FormArranger formArranger;
    private Composite composite;
    private ScrolledComposite sComposite;
    private int widgetRowCount;
    private List<String> unreferencedVariables;

    private static final int MAX_WIDGET_HEIGHT = 30; // TODO derive this instead
    public static final String ID = "perRuleProperties";

    public static final Map<Class<?>, EditorFactory<?>> EDITOR_FACTORIES_BY_PROPERTY_TYPE;

    static {
        Map<Class<?>, EditorFactory<?>> factoriesByPropertyType = new HashMap<Class<?>, EditorFactory<?>>();

        factoriesByPropertyType.put(Boolean.class, BooleanEditorFactory.INSTANCE);
        factoriesByPropertyType.put(String.class, StringEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Integer.class, IntegerEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Float.class, FloatEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Double.class, DoubleEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Object.class, EnumerationEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Character.class, CharacterEditorFactory.INSTANCE);

        factoriesByPropertyType.put(Class.class, TypeEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Class[].class, MultiTypeEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Method.class, MethodEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Method[].class, MultiMethodEditorFactory.INSTANCE);
        factoriesByPropertyType.put(String[].class, MultiStringEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Integer[].class, MultiIntegerEditorFactory.INSTANCE);
        factoriesByPropertyType.put(Object[].class, MultiEnumerationEditorFactory.INSTANCE);

        factoriesByPropertyType.put(File.class, FileEditorFactory.INSTANCE);
        // factoriesByPropertyType.put(Package.class,
        // PackageEditorFactory.instance);

        EDITOR_FACTORIES_BY_PROPERTY_TYPE = Collections.unmodifiableMap(factoriesByPropertyType);
    }

    public PerRulePropertyPanelManager(String theTitle, EditorUsageMode theMode, ValueChangeListener theListener) {
        super(ID, theTitle, theMode, theListener);
    }

    protected boolean canManageMultipleRules() {
        return false;
    }

    protected boolean canWorkWith(Rule rule) {

        // TODO if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) return true;
        // won't work, need to tweak Rule implementation as map is empty

        // alternate approach for now
        for (PropertyDescriptor<?> desc : rule.getPropertyDescriptors()) {
            if (desc.equals(XPathRule.XPATH_DESCRIPTOR)) {
                return true;
            }
        }

        return !Configuration.filteredPropertiesOf(rule).isEmpty();
    }

    protected void clearControls() {
        formArranger.clearChildren();
    }

    public void loadValues() {
        formArranger.loadValues();
    }

    public void showControls(boolean flag) {

        clearControls();
    }

    /*
     * We want to intercept this and update the tab if we detect problems after
     * we pass it on..
     */
    private ValueChangeListener chainedListener() {

        return FormArranger.chain(changeListener, new ValueChangeListener() {

            public void changed(RuleSelection rule, PropertyDescriptor<?> desc, Object newValue) {
                updateUI();
            }

            public void changed(PropertySource source, PropertyDescriptor<?> desc, Object newValue) {
                updateUI();
            }

        });
    }

    public Control setupOn(Composite parent) {

        sComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        composite = new Composite(sComposite, SWT.NONE);

        sComposite.setContent(composite);
        sComposite.setExpandHorizontal(true);
        sComposite.setExpandVertical(true);

        formArranger = new FormArranger(composite, EDITOR_FACTORIES_BY_PROPERTY_TYPE, chainedListener(), this);

        return sComposite;
    }

    public void addedRows(int rowCountDelta) {
        widgetRowCount += rowCountDelta;
        adjustMinimumHeight();
    }

    private void adjustMinimumHeight() {
        sComposite.setMinSize(composite.computeSize(500, widgetRowCount * MAX_WIDGET_HEIGHT));
    }

    protected void adapt() {

        widgetRowCount = formArranger.arrangeFor(soleRule());
        validate();

        if (widgetRowCount < 0) {
            return;
        }

        adjustMinimumHeight();
    }

    public boolean validate() {
        if (!super.validate()) {
            return false;
        }

        // any unref'd vars are not real errors
        unreferencedVariables = formArranger.updateDeleteButtons(); 

        return true;
    }

    protected List<String> fieldWarnings() {

        List<String> warnings = new ArrayList<String>(2);

        if (rules != null && !canManageMultipleRules()) { // TODO can do better
            Rule soleRule = soleRule();
            if (soleRule != null) {
                String dysfunctionReason = soleRule.dysfunctionReason();
                if (dysfunctionReason != null) {
                    warnings.add(dysfunctionReason);
                }
            }
        }

        if (unreferencedVariables == null || unreferencedVariables.isEmpty()) {
            return warnings;
        }

        warnings.add("Unreferences variables: " + unreferencedVariables);

        return warnings;
    }
}

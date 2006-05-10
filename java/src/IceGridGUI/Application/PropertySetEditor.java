// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
package IceGridGUI.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;

import IceGrid.*;
import IceGridGUI.*;

class PropertySetEditor extends Editor
{
    protected void applyUpdate()
    {
	PropertySet nps = (PropertySet)_target;
	Root root = nps.getRoot();

	root.disableSelectionListener();
	try
	{
	    PropertySetParent parent = (PropertySetParent)nps.getParent();
	    if(nps.isEphemeral())
	    {
		writeDescriptor();
		PropertySetDescriptor descriptor = 
		    (PropertySetDescriptor)nps.getDescriptor();
		nps.destroy(); // just removes the child
		
		try
		{
		    parent.tryAdd(_id.getText(), descriptor);
		}
		catch(UpdateFailedException e)
		{
		    //
		    // Add back ephemeral child
		    //
		    try
		    {
			parent.insertPropertySet(nps, true);
		    }
		    catch(UpdateFailedException die)
		    {
			assert false;
		    }
		    root.setSelectedNode(_target);
		    
		    JOptionPane.showMessageDialog(
			root.getCoordinator().getMainFrame(),
			e.toString(),
			"Apply failed",
			JOptionPane.ERROR_MESSAGE);
		    return;
		}

		//
		// Success
		//
		_target = ((TreeNode)parent).findChildWithDescriptor(descriptor);
		root.updated();
		root.setSelectedNode(_target);
		_id.setEditable(false);
	    }
	    else
	    {
		writeDescriptor();
		root.updated();
		nps.getEditable().markModified();
	    }
	    
	    root.getCoordinator().getCurrentTab().showNode(_target);
	    _applyButton.setEnabled(false);
	    _discardButton.setEnabled(false);
	}
	finally
	{
	    root.enableSelectionListener();
	}
    }

    Utils.Resolver getDetailResolver()
    {
	Root root = _target.getRoot();

	if(root.getCoordinator().substitute())
	{
	    return _target.getResolver();
	}
	else
	{
	    return null;
	}
    }

    PropertySetEditor(JFrame parentFrame)
    {
	//
	// Associate updateListener with various fields
	//
	_id.getDocument().addDocumentListener(_updateListener);
	_id.setToolTipText("The id of this Property Set");
	
	_propertySets.setEditable(false);
	_properties = new PropertiesField(this);

	_propertySetsDialog = new ListDialog(parentFrame, 
					     "Property Set References", true);

	Action openPropertySetsDialog = new AbstractAction("...")
	    {
		public void actionPerformed(ActionEvent e) 
		{
		    java.util.LinkedList result = _propertySetsDialog.show(
			_propertySetsList, getProperties());
		    if(result != null)
		    {
			updated();
			_propertySetsList = result;
			setPropertySetsField();
		    }
		}
	    };
	openPropertySetsDialog.putValue(Action.SHORT_DESCRIPTION,
					"Edit property set references");
	_propertySetsButton = new JButton(openPropertySetsDialog);
    }
    
    void writeDescriptor()
    {
	PropertySetDescriptor descriptor = 
	    (PropertySetDescriptor)getPropertySet().getDescriptor();

	descriptor.references = 
	    (String[])_propertySetsList.toArray(new String[0]);
	descriptor.properties = _properties.getProperties();
    }	    
    
    boolean isSimpleUpdate()
    {
	return true;
    }

    protected void appendProperties(DefaultFormBuilder builder)
    {
	builder.append("ID" );
	builder.append(_id, 3);
	builder.nextLine();
	
	builder.append("Property Sets");
	builder.append(_propertySets, _propertySetsButton);
	builder.nextLine();

	builder.append("Properties");
	builder.nextLine();
	builder.append("");
	builder.nextLine();
	builder.append("");

	builder.nextLine();
	builder.append("");

	builder.nextRow(-6);
	JScrollPane scrollPane = new JScrollPane(_properties);
	CellConstraints cc = new CellConstraints();
	builder.add(scrollPane, 
		    cc.xywh(builder.getColumn(), builder.getRow(), 3, 7));
	builder.nextRow(6);
	builder.nextLine();
    }

    protected void buildPropertiesPanel()
    {
	super.buildPropertiesPanel();
	_propertiesPanel.setName("Named Property Set");
    }
  
    void show(PropertySet nps)
    {
	detectUpdates(false);
	_target = nps;

	Utils.Resolver resolver = getDetailResolver();
	boolean isEditable = (resolver == null);
	
	PropertySetDescriptor descriptor = 
	    (PropertySetDescriptor)nps.getDescriptor();
	
	_id.setText(_target.getId());
	_id.setEditable(_target.isEphemeral());
	
	_propertySetsList = java.util.Arrays.asList(descriptor.references);
	setPropertySetsField();
	_propertySetsButton.setEnabled(isEditable);

	_properties.setProperties(descriptor.properties, null, 
				  getDetailResolver(), isEditable);

	_applyButton.setEnabled(nps.isEphemeral());
	_discardButton.setEnabled(nps.isEphemeral());	  
	detectUpdates(true);
    }

    private PropertySet getPropertySet()
    {
	return (PropertySet)_target;
    }

    private void setPropertySetsField()
    {
	final Utils.Resolver resolver = getDetailResolver();
	
	Ice.StringHolder toolTipHolder = new Ice.StringHolder();
	Utils.Stringifier stringifier = new Utils.Stringifier()
	    {
		public String toString(Object obj)
		{
		    return Utils.substitute((String)obj, resolver);
		}
	    };
	
	_propertySets.setText(
	    Utils.stringify(_propertySetsList, 
			    stringifier, ", ", toolTipHolder));

	String toolTip = "<html>Property Sets";

	if(toolTipHolder.value != null)
	{
	    toolTip += ":<br>" + toolTipHolder.value;
	}
	toolTip += "</html>";
	_propertySets.setToolTipText(toolTip);
    }

    private JTextField _id = new JTextField(20);
    
    private JTextField _propertySets = new JTextField(20);
    private java.util.List _propertySetsList;
    private ListDialog _propertySetsDialog;
    private JButton _propertySetsButton;
 
    private PropertiesField _properties;  
}

package com.rodriguemouadeu.vaadinapp;

import com.rodriguemouadeu.vaadinapp.model.Customer;
import com.rodriguemouadeu.vaadinapp.model.CustomerStatus;
import com.rodriguemouadeu.vaadinapp.service.CustomerService;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction.KeyCode;

import CustomerFormDesign.CustomerFormDesign;

public class CustomerForm extends CustomerFormDesign {

	CustomerService service = CustomerService.getInstance();
	private Customer customer;
	private MyUI myUI;

	public CustomerForm(MyUI myUI) {
		this.myUI = myUI;

		// populate combobox field
		status.addItems(CustomerStatus.values());
		
		// set primary key shortcut
		save.setClickShortcut(KeyCode.ENTER);
		
		// bind buttons
		save.addClickListener(e->this.save());
		delete.addClickListener(e->this.delete());
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
		BeanFieldGroup.bindFieldsUnbuffered(customer, this);

		// Show delete button for only customers already in the database
		delete.setVisible(customer.isPersisted());
		setVisible(true);
		firstName.selectAll();
	}
	
	private void delete() {
	    service.delete(customer);
	    myUI.updateList();
	    setVisible(false);
	}

	private void save() {
	    service.save(customer);
	    myUI.updateList();
	    setVisible(false);
	}
}

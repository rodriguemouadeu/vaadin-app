package com.rodriguemouadeu.vaadinapp;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.rodriguemouadeu.vaadinapp.model.Customer;
import com.rodriguemouadeu.vaadinapp.service.CustomerService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("com.rodriguemouadeu.vaadinapp.MyAppWidgetset")
public class MyUI extends UI {

	private CustomerService service = CustomerService.getInstance();
	private Grid grid;
	private TextField filterText;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		/*
		 * final VerticalLayout layout = new VerticalLayout();
		 * 
		 * final TextField name = new TextField(); name.setCaption(
		 * "Type your name here:");
		 * 
		 * Button button = new Button("Click Me"); button.addClickListener( e ->
		 * { layout.addComponent(new Label("Thanks " + name.getValue() +
		 * ", it works!")); });
		 * 
		 * layout.addComponents(name, button); layout.setMargin(true);
		 * layout.setSpacing(true);
		 * 
		 * setContent(layout);
		 */

		final VerticalLayout layout = new VerticalLayout();
		
		filterText = new TextField();
		filterText.setInputPrompt("filter by name...");
		filterText.addTextChangeListener(e -> {
			grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, service.findAll(e.getText())));
		});
		
		Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
		clearFilterTextBtn.setDescription("Clear the current filter");
		clearFilterTextBtn.addClickListener(e -> {
		  filterText.clear();
		  updateList();
		});

		CssLayout filtering = new CssLayout();
		filtering.addComponents(filterText, clearFilterTextBtn);
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		
		grid = new Grid();
		grid.setColumns("firstName", "lastName", "email");
		// add Grid to the layout


		CustomerForm form = new CustomerForm(this);
		form.setVisible(false);
		Button addCustomerBtn = new Button("Add new customer");
		addCustomerBtn.addClickListener(e -> {
		    grid.select(null);
		    form.setCustomer(new Customer());
		});
		
		HorizontalLayout toolbar = new HorizontalLayout(filtering, addCustomerBtn);
		toolbar.setSpacing(true);
		
		HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSpacing(true);
		main.setSizeFull();
		grid.setSizeFull();
		main.setExpandRatio(grid, 1);

		layout.addComponents(toolbar, main);

		// fetch list of Customers from service and assign it to Grid
		List<Customer> customers = service.findAll();
		grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customers));
		
		layout.setMargin(true);
		setContent(layout);
		
		grid.addSelectionListener(event -> {
		    if (event.getSelected().isEmpty()) {
		        form.setVisible(false);
		    } else {
		        Customer customer = (Customer) event.getSelected().iterator().next();
		        form.setCustomer(customer);
		    }
		});

	}

	public void updateList() {
		List<Customer> customers = service.findAll(filterText.getValue());
		grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customers));
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}

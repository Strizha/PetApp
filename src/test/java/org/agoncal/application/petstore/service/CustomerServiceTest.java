package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.model.Customer;
import org.agoncal.application.petstore.service.CustomerService;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

@RunWith(Arquillian.class)
public class CustomerServiceTest
{

   @Inject
   private CustomerService customerservice;

   @Deployment
   public static JavaArchive createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class)
            .addClass(AbstractService.class)
            .addClass(CustomerService.class)
            .addClass(Customer.class)
            .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Test
   public void should_be_deployed()
   {
      Assert.assertNotNull(customerservice);
   }

   @Test
   public void should_crud()
   {
      // Gets all the objects
      int initialSize = customerservice.listAll().size();

      // Creates an object
      Customer customer = new Customer();
      customer.setFirstName("Dummy value");

      // Inserts the object into the database
      customer = customerservice.persist(customer);
      assertNotNull(customer.getId());
      assertEquals(initialSize + 1, customerservice.listAll().size());

      // Finds the object from the database and checks it's the right one
      customer = customerservice.findById(customer.getId());
      assertEquals("Dummy value", customer.getFirstName());

      // Updates the object
      customer.setFirstName("A new value");
      customer = customerservice.merge(customer);

      // Finds the object from the database and checks it has been updated
      customer = customerservice.findById(customer.getId());
      assertEquals("A new value", customer.getFirstName());

      // Deletes the object from the database and checks it's not there anymore
      customerservice.remove(customer);
      assertEquals(initialSize, customerservice.listAll().size());
   }
}
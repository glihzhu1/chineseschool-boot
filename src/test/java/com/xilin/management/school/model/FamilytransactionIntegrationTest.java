package com.xilin.management.school.model;
import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext-test*.xml")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class FamilytransactionIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    FamilytransactionDataOnDemand dod;

	@Autowired
    FamilytransactionRepository familytransactionRepository;

	@Test
    public void testCount() {
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", dod.getRandomFamilytransaction());
        long count = familytransactionRepository.count();
        Assert.assertTrue("Counter for 'Familytransaction' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFind() {
        Familytransaction obj = dod.getRandomFamilytransaction();
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", obj);
        Integer id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to provide an identifier", id);
        obj = familytransactionRepository.findOne(id);
        Assert.assertNotNull("Find method for 'Familytransaction' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Familytransaction' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAll() {
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", dod.getRandomFamilytransaction());
        long count = familytransactionRepository.count();
        Assert.assertTrue("Too expensive to perform a find all test for 'Familytransaction', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Familytransaction> result = familytransactionRepository.findAll();
        Assert.assertNotNull("Find all method for 'Familytransaction' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Familytransaction' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindEntries() {
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", dod.getRandomFamilytransaction());
        long count = familytransactionRepository.count();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Familytransaction> result = familytransactionRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
        Assert.assertNotNull("Find entries method for 'Familytransaction' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Familytransaction' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testSave() {
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", dod.getRandomFamilytransaction());
        Familytransaction obj = dod.getNewTransientFamilytransaction(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Familytransaction' identifier to be null", obj.getId());
        try {
        	familytransactionRepository.save(obj);
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        familytransactionRepository.flush();
        Assert.assertNotNull("Expected 'Familytransaction' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testDelete() {
        Familytransaction obj = dod.getRandomFamilytransaction();
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to initialize correctly", obj);
        Integer id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Familytransaction' failed to provide an identifier", id);
        obj = familytransactionRepository.findOne(id);
        familytransactionRepository.delete(obj);
        familytransactionRepository.flush();
        Assert.assertNull("Failed to remove 'Familytransaction' with identifier '" + id + "'", familytransactionRepository.findOne(id));
    }
}

package ru.sip64.webfax.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.PersistenceUnit;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.jboss.logging.Logger;

import ru.sip64.webfax.db.PinCode;
import ru.sip64.webfax.utils.Constants;

/**
 * Session Bean implementation class AccessForDBPinCodeBean
 */
@Stateless
public class AccessForDBPinCodeBean implements AccessForDBPinCodeBeanLocal {

    /**
     * Default constructor. 
     */
	@PersistenceUnit
	private SessionFactory  sessionFactory  = null;
	
	private static final Logger log = Logger.getLogger(AccessForDBPinCodeBean.class.getName());
	
    public AccessForDBPinCodeBean() {
       
    }

	@Override
	public String getPinCode(String userName) {
		Session session = getSession();
		try {
			session.getTransaction().begin();
			Criteria criteria = session.createCriteria(PinCode.class);
			criteria.add(Restrictions.eq("username", userName));
			
			@SuppressWarnings("unchecked")
			List<PinCode> result = criteria.list();
			if (result.size() > 0){
				return result.get(0).getPinCode();
			}
			return Constants.BAD_USERNAME;
		}catch (RuntimeException e) {
	        session.getTransaction().rollback();
	        throw e;
	    }finally{
	    	session.close();
	    }
	}
	
	@Override
	public String getPinCodePassword(String userName) {
		Session session = getSession();
		try {
			session.getTransaction().begin();
			Criteria criteria = session.createCriteria(PinCode.class);
			criteria.add(Restrictions.eq("username", userName));
			
			@SuppressWarnings("unchecked")
			List<PinCode> result = criteria.list();
			if (result.size() > 0){
				return result.get(0).getPinPassword();
			}
			return null;
		}catch (RuntimeException e) {
	        session.getTransaction().rollback();
	        throw e;
	    }finally{
	    	session.close();
	    }
	}
	
	@Override
	public void createNewUser(String username, String pinCode,
			String pinPassword) {
		PinCode newPinCode = new PinCode();
		newPinCode.setUsername(username);
		newPinCode.setPinCode(pinCode);
		newPinCode.setPinPassword(pinPassword);
		Session session = getSession();
		try {
		session.getTransaction().begin();
	    session.saveOrUpdate(newPinCode);
	    session.flush();
	    session.getTransaction().commit();
	    log.info("USER " + newPinCode.getUsername() + " ADD OR UPDATE!");
		}
	    catch (RuntimeException e) {
	        session.getTransaction().rollback();
	        log.error("USER " + newPinCode.getUsername() + " NOT ADD OR UPDATE!");
	        throw e;
	    }finally{
	    	session.close();
	    }
	}
    
	@Override
	public void updateUser(String username, String pinCode, String pinPassword){
		Session session = getSession();
		System.out.println(username+": "+ pinCode+": "+ pinPassword+": ");
		try {
		session.beginTransaction();
		String hqlUpdate = "update PinCode set " +
				"pin_code = :newPinCode, pin_password = :newPinPassword  " +
				"where username = :oldUsername";
	    session.createQuery(hqlUpdate).setString("oldUsername", username).
	    							   setString("newPinCode", pinCode).
	    							   setString("newPinPassword", pinPassword).executeUpdate();
	    log.info("USER " + username + " UPDATE!");
		}
	    catch (RuntimeException e) {
	        session.getTransaction().rollback();
	        log.error("USER " + username + " NOT UPDATE!");
	        throw e;
	    }finally{
	    	session.close();
	    }
	}
	
	@Override
	public void deleteUser(String username){
		Session session = getSession();
		try {
		session.beginTransaction();
		String hqlDelete = "delete PinCode pin where pin.username = :oldUsername";
	    session.createQuery(hqlDelete).setString("oldUsername", username).executeUpdate();
	    log.info("USER " + username + " DELETE!");
		}
	    catch (RuntimeException e) {
	        session.getTransaction().rollback();
	        log.error("USER " + username + " NOT DELETE!");
	        throw e;
	    }finally{
	    	session.close();
	    }
	}
	
	private Session getSession() {
		Session session = sessionFactory.getCurrentSession();
        if (session.isOpen() == false) {
            session = sessionFactory.openSession();
        }
        return session;
    }

	

}

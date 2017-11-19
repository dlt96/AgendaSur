/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agendasur.ejb;

import agendasur.entity.Evento;
import agendasur.entity.Tag;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Adrian
 */
@Stateless
public class EventoFacade extends AbstractFacade<Evento> {

    @PersistenceContext(unitName = "AgendaSur-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFacade() {
        super(Evento.class);
    }
    
    public List<Evento> findEventosNoValidados(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        Query q;
        q = this.em.createQuery("select e from Evento e where e.validado = false and e.fechafin >= :currentDate");
        q.setParameter("currentDate", formatter.format(currentDate));
        return q.getResultList();
    }
    
    public List<Evento> findEventosByTag(Tag tag){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        Query q;
        q = this.em.createQuery("select e from Evento e where :tag MEMBER OF e.tagList and e.validado = true and e.fechafin >= :currentDate");
        q.setParameter("tag", tag);
        q.setParameter("currentDate", formatter.format(currentDate));
        return q.getResultList();
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agendasur.ejb;

import agendasur.entity.Evento;
import agendasur.entity.Tag;
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
public class TagFacade extends AbstractFacade<Tag> {

    @PersistenceContext(unitName = "AgendaSur-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagFacade() {
        super(Tag.class);
    }

    public List<Evento> getEventos(String nombre) {
        Query q;
        q = this.em.createQuery("select t from Tag t where t.nombre = :tag ");
        q.setParameter("tag", nombre);
        Tag tag = (Tag) q.getResultList().get(0);
        return tag.getEventoList();
    }   
}

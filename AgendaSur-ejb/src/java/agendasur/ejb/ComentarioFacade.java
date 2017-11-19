/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agendasur.ejb;

import agendasur.entity.Comentario;
import java.util.ArrayList;
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
public class ComentarioFacade extends AbstractFacade<Comentario> {

    @PersistenceContext(unitName = "AgendaSur-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComentarioFacade() {
        super(Comentario.class);
    }

    public List<Comentario> getComentarios(int id) {
        Query q;
        q = this.em.createQuery("select c from Comentario c where c.comentarioPK.eventoId = :id ");
        q.setParameter("id", id);
        List<Comentario> list = q.getResultList();
        return list;
    }
    
}

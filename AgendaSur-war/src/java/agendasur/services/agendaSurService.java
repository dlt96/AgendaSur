/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agendasur.services;

import agendasur.ejb.ComentarioFacade;
import agendasur.ejb.EventoFacade;
import agendasur.ejb.TagFacade;
import agendasur.ejb.UsuarioFacade;
import agendasur.entity.Comentario;
import agendasur.entity.Evento;
import agendasur.entity.Usuario;
import agendasur.entity.Tag;
import agendasur.location.Distancia;
import agendasur.mail.Mail;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author dlope
 */
@WebService(serviceName = "agendaSurService")
public class agendaSurService {

    @EJB
    private EventoFacade ejbEvento;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")
    @EJB
    private ComentarioFacade ejbComentario;

    @EJB
    private TagFacade ejbTag;
    @EJB
    private UsuarioFacade ejbUsuario;

    @WebMethod(operationName = "createEvento")
    @Oneway
    public void createEvento(@WebParam(name = "entity") Evento entity) {
        ejbEvento.create(entity);
    }
    
    @WebMethod(operationName = "editEvento")
    @Oneway
    public void editEvento(@WebParam(name = "entity") Evento entity) {
        ejbEvento.edit(entity);
    }

    @WebMethod(operationName = "removeEvento")
    @Oneway
    public void removeEvento(@WebParam(name = "entity") Evento entity) {
        ejbEvento.remove(entity);
    }

    @WebMethod(operationName = "findEvento")
    public Evento findEvento(@WebParam(name = "id") Object id) {
        return ejbEvento.find(id);
    }

    @WebMethod(operationName = "findEventosByTag")
    public List<Evento> findEventosByTag(Tag tag) {
        return ejbEvento.findEventosByTag(tag);
    }

    @WebMethod(operationName = "findComentariosEvento")
    public List<Comentario> findComentariosEvento(int id) {
        return ejbComentario.getComentarios(id);
    }

    @WebMethod(operationName = "findAllEvento")
    public List<Evento> findAllEvento() {
        return ejbEvento.findAll();
    }

    /*@WebMethod(operationName = "findRangeEvento")
    public List<Evento> findRangeEvento(@WebParam(name = "range") int[] range) {
        return ejbEvento.findRange(range);
    }*/

    /*@WebMethod(operationName = "countEvento")
    public int countEvento() {
        return ejbEvento.count();
    }*/

    @WebMethod(operationName = "asignarTagsAUsuario")
    public void asignarTagsAUsuario(String email, List<String> listTagsString) {
        //Borrar el antiguo usuario de tag
        Usuario u = findUsuario(email);
        for(Tag t : u.getTagList()){
            if(!listTagsString.contains(t.getNombre())){
                t.getUsuarioList().remove(u);
                ejbTag.edit(t);
            }
        }
        List<Tag> listTags = new ArrayList<>();
        listTagsString.forEach(tagId -> listTags.add(this.findTag(tagId)));
        u.setTagList(listTags);

        for (Tag tag : listTags) {
            if (tag.getUsuarioList() == null) {
                tag.setUsuarioList(new LinkedList<>());
            }
            if (!tag.getUsuarioList().contains(u)) {
                tag.getUsuarioList().add(u);
                ejbTag.edit(tag);
            }
        }
        ejbUsuario.edit(u);
    }
    
    @WebMethod(operationName = "asignarTagsAEvento")
    public void asignarTagsAEvento(Evento evento, List<String> listTagsString) {
        //Borrar el antiguo tags
        evento = findEvento(evento.getId());
        for(Tag t : evento.getTagList()){
            if(!listTagsString.contains(t.getNombre())){
                t.getEventoList().remove(evento);
                ejbTag.edit(t);
            }
        }
        
        List<Tag> listTags = new ArrayList<>();
        listTagsString.forEach(tagId -> listTags.add(this.findTag(tagId)));
        evento.setTagList(listTags);

        for (Tag tag : listTags) {
            if (tag.getEventoList() == null) {
                tag.setEventoList(new LinkedList<>());
            }
            if (!tag.getEventoList().contains(evento)) {
                tag.getEventoList().add(evento);
                ejbTag.edit(tag);
            }
        }
        ejbEvento.edit(evento);
    }

    @WebMethod(operationName = "findEventosNoCaducadosYValidados")
    public List<Evento> findEventosNoCaducadosYValidados() {
        return ejbEvento.findEventosNoCaducadosYValidados();
    }

    @WebMethod(operationName = "findEventosNoValidados")
    public List<Evento> findEventosNoValidados() {
        return ejbEvento.findEventosNoValidados();
    }

    @WebMethod(operationName = "darMeGusta")
    public void darMeGusta(Evento evento, Usuario usuario) {
        List<Usuario> meGustasEvento = evento.getUsuarioList();
        meGustasEvento.add(usuario);
        evento.setUsuarioList(meGustasEvento);

        List<Evento> meGustasUsuario = usuario.getEventoList();
        meGustasUsuario.add(evento);
        usuario.setEventoList(meGustasUsuario);

        ejbEvento.edit(evento);
        ejbUsuario.edit(usuario);
    }

    @WebMethod(operationName = "findEventosOrdenadosPorDistancia")
    public List<Evento> findEventosOrdenadosPorDistancia(double longitud, double latitud) {
        List<Evento> listaEventos = this.findEventosNoCaducadosYValidados();
        listaEventos.sort((evento1, evento2)
                -> Double.compare(Distancia.getDistancia(evento2.getLatitud(), evento2.getLongitud(), latitud, longitud),
                        Distancia.getDistancia(evento1.getLatitud(), evento1.getLongitud(), latitud, longitud)));
        return listaEventos;
    }

    @WebMethod(operationName = "existeMeGusta")
    public boolean existeMeGusta(Evento evento, Usuario usuario) {
        return ejbEvento.existeMegusta(evento, usuario);
        //return evento.getUsuarioList().contains(usuario) && usuario.getEventoList().contains(evento);
    }
    
    @WebMethod(operationName = "validarEvento")
    public void validarEvento(int id){
        Evento e = this.findEvento(id);
        e.setValidado(true);
        ejbEvento.edit(e);
        this.sendMail(id);
    }

    //COMENTARIO
  
    @WebMethod(operationName = "createComentario")
    @Oneway
    public void createComentario(@WebParam(name = "entity") Comentario entity) {
        ejbComentario.create(entity);
    }

    /*@WebMethod(operationName = "editComentario")
    @Oneway
    public void editComentario(@WebParam(name = "entity") Comentario entity) {
        ejbComentario.edit(entity);
    }

    @WebMethod(operationName = "removeComentario")
    @Oneway
    public void removeComentario(@WebParam(name = "entity") Comentario entity) {
        ejbComentario.remove(entity);
    }

    @WebMethod(operationName = "findComentario")
    public Comentario findComentario(@WebParam(name = "id") Object id) {
        return ejbComentario.find(id);
    }

    @WebMethod(operationName = "findAllComentario")
    public List<Comentario> findAllComentario() {
        return ejbComentario.findAll();
    }

    @WebMethod(operationName = "findRangeComentario")
    public List<Comentario> findRangeComentario(@WebParam(name = "range") int[] range) {
        return ejbComentario.findRange(range);
    }

    @WebMethod(operationName = "countComentario")
    public int countComentario() {
        return ejbComentario.count();
    }

    //USUARIO
    @WebMethod(operationName = "createUsuario")
    @Oneway
    public void createUsuario(@WebParam(name = "entity") Usuario entity) {
        ejbUsuario.create(entity);
    }*/

    @WebMethod(operationName = "editUsuario")
    @Oneway
    public void editUsuario(@WebParam(name = "entity") Usuario entity) {
        ejbUsuario.edit(entity);
    }

    /*@WebMethod(operationName = "removeUsuario")
    @Oneway
    public void removeUsuario(@WebParam(name = "entity") Usuario entity) {
        ejbUsuario.remove(entity);
    }*/

    @WebMethod(operationName = "findUsuario")
    public Usuario findUsuario(@WebParam(name = "id") Object id) {
        return ejbUsuario.find(id);
    }

    /*@WebMethod(operationName = "findAllUsuario")
    public List<Usuario> findAllUsuario() {
        return ejbUsuario.findAll();
    }

    @WebMethod(operationName = "findRangeUsuario")
    public List<Usuario> findRangeUsuario(@WebParam(name = "range") int[] range) {
        return ejbUsuario.findRange(range);
    }

    @WebMethod(operationName = "countUsuario")
    public int countUsuario() {
        return ejbUsuario.count();
    }*/

//TAG
    /*@WebMethod(operationName = "createTag")
    @Oneway
    public void createTag(@WebParam(name = "entity") Tag entity) {
        ejbTag.create(entity);
    }*/

    @WebMethod(operationName = "editTag")
    @Oneway
    public void editTag(@WebParam(name = "entity") Tag entity) {
        ejbTag.edit(entity);
    }

    /*@WebMethod(operationName = "removeTag")
    @Oneway
    public void removeTag(@WebParam(name = "entity") Tag entity) {
        ejbTag.remove(entity);
    }*/

    @WebMethod(operationName = "findTag")
    public Tag findTag(@WebParam(name = "id") Object id) {
        return ejbTag.find(id);
    }

    @WebMethod(operationName = "findAllTag")
    public List<Tag> findAllTag() {
        return ejbTag.findAll();
    }

    /*@WebMethod(operationName = "findRangeTag")
    public List<Tag> findRangeTag(@WebParam(name = "range") int[] range) {
        return ejbTag.findRange(range);
    }

    @WebMethod(operationName = "countTag")
    public int countTag() {
        return ejbTag.count();
    }*/

    @WebMethod(operationName = "sendMail")
    public void sendMail(int id) {
        Evento e = findEvento(id);
        Mail.sendMail(e);
    }

    /**
     * Web service operation
     * @param email
     * @return 
     */
    
    @WebMethod(operationName = "findTagsUsuario")
    public List<Tag> findTagsUsuario(String email) {
        return ejbTag.findTagsByUser(findUsuario(email));
    }
    
    @WebMethod(operationName = "findTagsEvento")
    public List<Tag> findTagsEvento(int id) {
        return ejbTag.findTagsByEvento(findEvento(id));
    }
}

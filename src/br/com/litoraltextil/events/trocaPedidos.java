package br.com.litoraltextil.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.ReinfUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;

import static br.com.litoraltextil.utils.Utils.AlteraProdutoPedido;
import static br.com.sankhya.jape.dao.JdbcWrapper.closeSession;

public class trocaPedidos implements EventoProgramavelJava {
    public static final String LOG_PREFIX = "DE PARA [LITORAL] - Evento : trocaPedidos : ";

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        System.out.println(LOG_PREFIX + "Iniciando afterInsert...");
        DynamicVO logDeParaVO = (DynamicVO) event.getVo();
        try {
            AlteraProdutoPedido(logDeParaVO);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(LOG_PREFIX + "Erro ao executar o afterInsert: " + e.getMessage());
        }
    }

    @Override   
    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}

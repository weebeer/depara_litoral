package br.com.litoraltextil.utils;

import br.com.litoraltextil.dao.*;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Utils {
    public static void AlteraProdutoPedido(DynamicVO logDePara, BigDecimal codusu) throws Exception {
        JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        try {
            BigDecimal codprodant = logDePara.asBigDecimal("CODPRODANT");
            BigDecimal codprodins = logDePara.asBigDecimal("CODPRODINS");
            BigDecimal codproj = logDePara.asBigDecimal("CODPROJ");
//            String corAnterior = logDePara.asString("CODCORANT");
//            String corInserida = logDePara.asString("CODCORINS");
//            String nomeProduto = logDePara.asString("PRODUTOANT");
//            Timestamp dataAlteracao = logDePara.asTimestamp("DTALTER");
            NativeSql sql = new NativeSql(jdbc);
            sql.appendSql("SELECT ITE.NUNOTA , ITE.SEQUENCIA FROM TGFITE ITE WHERE CODPROD = :CODPROD AND CONTROLE = :CONTROLE AND CODLOCALORIG = 1002 AND NOT EXISTS (SELECT 1 FROM TGFVAR WHERE NUNOTAORIG = ITE.NUNOTA)");
            sql.setNamedParameter("CODPROD", codprodant);
            sql.setNamedParameter("CONTROLE",codproj.toString());
            ResultSet rs = sql.executeQuery();
            while (rs.next()) {
                BigDecimal nunota = rs.getBigDecimal("NUNOTA");
                BigDecimal sequencia = rs.getBigDecimal("SEQUENCIA");

                JapeWrapper tgfite = JapeFactory.dao("ItemNota");
                tgfite.prepareToUpdateByPK(nunota, sequencia)
                        .set("CODPROD", codprodins)
                        .update();

                JapeWrapper logDeParaPedidoVenda = JapeFactory.dao("AD_DEPARAPEDVEN");
                logDeParaPedidoVenda.create()
                        .set("NUNOTA", nunota)
                        .set("CODPRODANT", codprodant)
                        .set("CODPRODINS", codprodins)
                        .set("CODPROJ", codproj.toString())
                        .set("SEQITE", sequencia)
                        .set("DTALTER", new Timestamp(System.currentTimeMillis()))
                        .set("CODUSU", codusu)
                        .save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbc.closeSession();
        }
    }

    public static void NotificarVendedor(BigDecimal nunota , String corAnt , String corIns , String prodAnt , Timestamp dtAlter) throws Exception {
        try {
            System.out.println("Iniciando notificação ao parceiro...");

            DynamicVO tgfcabVO = CabecalhoDAO.get(nunota);
            DynamicVO tgfparVO = ParceiroDAO.get(tgfcabVO.asBigDecimal("CODPARC"));
            BigDecimal nroForca = tgfcabVO.asBigDecimal("AD_PEDIDOID");
            String razaoSocial = tgfparVO.asString("RAZAOSOCIAL");
            BigDecimal codvend = tgfcabVO.asBigDecimal("CODVEND");
            StringBuilder emailConcatenado = new StringBuilder();
            emailConcatenado.append("francisco.silva@litoraltextil.com.br");
//            if (codvend != null && codvend.compareTo(BigDecimal.ZERO) > 0) {
//                DynamicVO vendedorVO = JapeFactory.dao("Vendedor").findByPK(codvend);
//                if (!vendedorVO.asString("EMAIL").trim().isEmpty()) {
//                    emailConcatenado.append(",");
//                    emailConcatenado.append(vendedorVO.asString("EMAIL").trim());
//                }
//            }

            String msgTratada = "<!DOCTYPE html><html><head><style>body{font-family: Arial, sans-serif;margin: 0;padding: 20px;background-color: #f5f5f5;}.container{max-width: 600px;margin: 0 auto;background-color: white;border-radius: 8px;overflow: hidden;box-shadow: 0 2px 10px rgba(0,0,0,0.1);}.header{padding: 20px;text-align: center;color: white;}.logo-text{font-size: 28px;font-weight: bold;letter-spacing: 2px;margin: 0;text-shadow: 2px 2px 4px rgba(0,0,0,0.3);}.tagline{font-size: 12px;margin: 5px 0 0 0;opacity: 0.9;}.content{padding: 30px;}h1{color: #1E3A8A;text-align: center;font-size: 20px;margin-bottom: 25px;border-bottom: 2px solid #FF6B35;padding-bottom: 10px;}p{line-height: 1.6;margin: 15px 0;color: #333;text-align: left;}.info-box{background-color: #f8f9fa;border-left: 4px solid #FF6B35;padding: 15px;margin: 20px 0;}.info-box p{margin: 5px 0;}.contact-box{background: white;color: #1E3A8A;padding: 20px;border-radius: 12px;text-align: center;margin: 20px 0;box-shadow: 0 10px 25px rgba(255, 107, 53, 0.2), 0 5px 10px rgba(30, 58, 138, 0.1);border: 1px solid;border-image: linear-gradient(45deg, #FF6B35, #1E3A8A, #FF6B35) 1;position: relative;overflow: hidden;}.contact-box::before{content: '';position: absolute;top: 0;left: 0;right: 0;bottom: 0;background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(30, 58, 138, 0.05) 100%);pointer-events: none;}.contact-box p{margin: 8px 0;}.footer{background-color: #f8f9fa;padding: 15px;text-align: center;font-size: 11px;color: #666;border-top: 1px solid #eee;}.highlight{color: #1E3A8A;font-weight: bold;}.accent{color: #FF6B35;font-weight: bold;}</style></head><body><div class=\\\"container\\\"><div class=\\\"header\\\"><div class=\\\"logo-container\\\"><img src=\\\"https://static.wixstatic.com/media/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png/v1/fill/w_397,h_149,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/75c56f_833850e0756a44ac8088240c9b16c165~mv2.png\\\" alt=\\\"Litoral Têxtil\\\" class=\\\"logo-img\\\"></div></div><div class=\\\"content\\\"><h1>Notificação de Alteração de Produto - Pedido Nº [nroForca]</h1><p>Prezado(a) Parceiro(a) <span class=\\\"highlight\\\">[razaoSocial]</span>,</p><p>Informamos que o pedido Nº <span class=\\\"highlight\\\">[nroForca]</span> teve alteração na especificação do produto <span class=\\\"accent\\\">[nomeProduto]</span>.</p><div class=\\\"info-box\\\"><p><strong>Alteração realizada:</strong> Troca de cor do tecido</p><p><strong>Cor anterior:</strong> <span class=\\\"highlight\\\">[corAnterior]</span></p><p><strong>Nova cor:</strong> <span class=\\\"accent\\\">[corInserida]</span></p><p><strong>Data da alteração:</strong> [dataAlteracao]</p></div><p>Solicitamos que verifiquem a disponibilidade do novo produto e confirmem o prazo de entrega atualizado.</p><div class=\\\"contact-box\\\"><p><strong>Para dúvidas ou confirmações:</strong></p><p>📞 Telefone: (27) 3333-4444</p><p>📧 Email: vendas@litoraltextil.com.br</p></div></div><div class=\\\"footer\\\"><p>Notificação enviada automaticamente pelo sistema Litoral Têxtil.<br>Em caso de problemas técnicos, contatar o setor de TI.</p></div></div></body></html>";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(dtAlter);

            msgTratada = msgTratada.replace("[nroForca]", nroForca.toString())
                                    .replace("[razaoSocial]", razaoSocial)
                                    .replace("[corAnterior]", corAnt)
                                    .replace("[corInserida]", corIns)
                                    .replace("[nomeProduto]", prodAnt)
                                    .replace("[dataAlteracao]", dataFormatada);
            JapeWrapper tmdfmgDAO = JapeFactory.dao(DynamicEntityNames.FILA_MSG);
            tmdfmgDAO.create()
                    .set("EMAIL",emailConcatenado)
                    .set("CODCON", BigDecimal.ZERO)
                    .set("CODMSG", null)
                    .set("STATUS", "Pendente")
                    .set("TIPOENVIO", "E")
                    .set("MAXTENTENVIO", BigDecimal.valueOf(3))
                    .set("ASSUNTO", "Notificação de Alteração de Produto - Pedido Nº " + nroForca)
                    .set("MENSAGEM", msgTratada.toCharArray())
                    .set("CODSMTP", BigDecimal.valueOf(10))
                    .save();

            System.out.println("Notificação enviada para o parceiro.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao notificar o parceiro: " + e.getMessage());
        }
    }

    public static String validarOperacao( BigDecimal nunota,BigDecimal sequencia, BigDecimal codProdAtual, BigDecimal codProdNovo, BigDecimal codProj) throws Exception {

        DynamicVO etiquetasVO = TabelasAD.CtrMtsDAO(nunota, codProdAtual);
        if (etiquetasVO != null) {
            return "Já existe etiqueta emitida pra esse número único/produto. Favor entrar em contato com o setor de expedição.";
        }

        DynamicVO itemNotaVO = ItemNotaDAO.get(nunota, sequencia);
        DynamicVO sortProdVO = TabelasAD.sortProdDAO(codProj, codProdNovo);
        if (itemNotaVO != null || sortProdVO != null) {
            return "O produto substituto já está no pedido de compra e/ou no sortimento do projeto.";
        }

        String ultimosQuatroDigitosProdNovo = codProdNovo.toString().substring(codProdNovo.toString().length() - 4);
        if ("0000".equals(ultimosQuatroDigitosProdNovo)) {
            return "Não é possível substituir o item por um produto PAI.";
        }

        String ultimosQuatroDigitosProdAtual = codProdAtual.toString().substring(codProdAtual.toString().length() - 4);
        if (!ultimosQuatroDigitosProdAtual.equals(ultimosQuatroDigitosProdNovo)) {
            return "Não é possivel alterar o item para um produto de um \"PAI\" diferente do projeto.";
        }

        DynamicVO produtoVO = ProdutoDAO.get(codProdNovo);
        if (produtoVO.asString("AD_LD").equals("S")){
            return "Não é possivel alterar o item para um produto LD.";
        }

        if (produtoVO.asString("ATIVO").equals("N")){
            return "Não é possivel alterar o item para um produto <b>INATIVO</b>.";
        }

        DynamicVO deParaVO = TabelasAD.deParaDAO(codProdNovo);
        if (deParaVO != null) {
            return "Não é possivel alterar o item para um produto já usado no mesmo lote.";
        }

        return null;
    }

}

sap.ui.define([
  "sap/ui/core/mvc/Controller",
  "sap/ui/model/json/JSONModel",
  "../model/formatter",
  "sap/m/Button",
  "sap/m/Dialog",
  "sap/ui/core/Core",
  "sap/m/Text",
  "sap/ui/layout/VerticalLayout",
  "sap/ui/layout/HorizontalLayout",
  "sap/m/library",
  "sap/m/List",
  "sap/m/StandardListItem",
  'sap/ui/core/Fragment',
  "sap/ui/core/TextAlign",
  'sap/ui/core/syncStyleClass',
  "sap/m/BusyIndicator",
  "sap/ui/core/library",
  "sap/m/MessageBox",
],
  /**
   * @param {typeof sap.ui.core.mvc.Controller} Controller
   */
  function (BaseController, JSONModel, formatter, Button, Dialog, Core, Text, VerticalLayout, HorizontalLayout, mobileLibrary, List, StandardListItem, Fragment, TextAlign, syncStyleClass, BusyIndicator, ValueState, MessageBox) {
    "use strict";
    sap.ui.getCore().getConfiguration().setFormatLocale("pt_BR");
    var ButtonType = mobileLibrary.ButtonType;
    var DialogType = mobileLibrary.DialogType;

    var baseUrl = 'https://new1-lpda9udk-first-mt-java-app.cfapps.us10-001.hana.ondemand.com/srv'
    // var baseUrl = 'https://run.mocky.io/v3/f85e6dce-ccb3-4fc8-8019-0e0ba39ac79c'
    var barcodesToPay = new Object()
    var listToShowReceipts = new Object()

    return BaseController.extend("payments.controller.App", {
      formatter: formatter,

      loadingOn(that) {
        var loading = new JSONModel({ loading: true });
        that.getView().setModel(loading, "loading")
      },

      loadingOff(that) {
        var loading = new JSONModel({ loading: false });
        that.getView().setModel(loading, "loading")
      },

      onInit() {
        this.loadDefaultList()
        this.getBalance(this)
        this.getView().setModel(new JSONModel({ isVisible: false }), "balanceVisible");
        this.getView().setModel(new JSONModel({ quantity: 0 }), "bankslips")
        this.getView().setModel(new JSONModel({ totalAmount: 0 }), "paymentAmount")
      },

      receiptsDialogShow: function () {
        this.receiptsDialog = new Dialog({
          title: "Lista de retornos",
          content: new List({
            noData: "Sem informações para apresentar",
            items: {
              path: "{batchResponse>/}",
              template: new StandardListItem({
                title: "{batchResponse>beneficiary/name}",
                icon: "sap-icon://receipt",
                description: "{keys>code}",
                infoState: "{= ${batchResponse>status}.length > 5 ? 'Error' : 'Success'}",
                info: "{= ${batchResponse>status}.length > 5 ? 'Falha' : 'Pago'}",
              })
            }
          }),
          beginButton: new Button({
            type: ButtonType.Emphasized,
            text: "Confirmar",
            press: function () {
              this.receiptsDialog.close();
            }.bind(this)
          })
        });

        // to get access to the controller's model
        this.getView().addDependent(this.receiptsDialog);
        this.receiptsDialog.open();
      },

      singleReceiptsDialogShow: function () {
        var response = this.getView().getModel("singleReceipt").oData
        this.singleReceiptsDialog = new Dialog({
          title: "Recibo",
          contentWidth: "550px",
          contentHeight: "200px",
          content: [
            new HorizontalLayout({
              content: [
                new VerticalLayout({
                  content: [                   
                    new Text({ text: "Data do pagamento: " }),
                    new Text({ text: "Valor pago: " }),
                    new Text({ text: "Nome do pagador: " }),
                    new Text({ text: "CNPJ pagador: " }),
                    new Text({ text: "Nome do beneficiário: " }),
                    new Text({ text: "CNPJ beneficiário: " }),
                    new Text({ text: "Conta do débito " }),
                    new Text({ text: "Agência: " }),
                    new Text({ text: "Conta: " }),
                    new Text({ text: "Código do boleto: " }),
                  ]
                }),

                new VerticalLayout({
                  content: [
                    new Text({ text: this.formatter.date(response.transaction.date) }),
                    new Text({ text: this.formatDoubleToBRL(response.nominalValue) }),
                    new Text({ text: response.payer.name }),
                    new Text({ text: this.formatter.cnpj(response.payer.documentNumber) }),
                    new Text({ text: response.beneficiary.beneficiaryName }),
                    new Text({ text: this.formatter.cnpj(response.beneficiary.beneficiaryDocument) }),
                    new Text({ text: "" }),
                    new Text({ text: response.debitAccount.branch }),
                    new Text({ text: response.debitAccount.number }),
                    new Text({ text: this.formatter.barcode(response.code) }),
                  ]
                })
              ]
            })
          ],
          beginButton: new Button({
            type: ButtonType.Emphasized,
            text: "Fechar",
            press: function () {
              this.singleReceiptsDialog.close();
            }.bind(this)
          })
        });

        // to get access to the controller's model
        this.singleReceiptsDialog.addStyleClass("sapUiResponsivePadding--content sapUiResponsivePadding--header sapUiResponsivePadding--footer sapUiResponsivePadding--subHeader");

        this.getView().addDependent(this.singleReceiptsDialog);
        this.singleReceiptsDialog.open();
      },


      loadDefaultList() {
        this.loadingOn(this)
        this.fastFilterDays(100)
        // this.loadResponse(this)
        this.loadResponsePayedBankslips(this)
      },

      handleChange: function (oEvent) {
        var from = oEvent.getParameter("from"),
          to = oEvent.getParameter("to");
        this.loadingOn(this)
        this.loadResponseDataFiltered(this, from.toISOString(), to.toISOString())
      },

      fastFilterDays: function (amountOfDays) {

        var today = new Date(Date.now());

        var dateOffset = 24 * 60 * 60 * 1000 * amountOfDays;
        var futureDate = new Date();
        futureDate.setTime(today.getTime() + dateOffset);

        this.loadingOn(this)
        this.loadResponseDataFiltered(this, today.toISOString(), futureDate.toISOString())
      },

      loadResponse(oView) {
        $.ajax({
          url: `${baseUrl}/payment/dda`,
          type: 'GET',
          success: function (res) {
            oView.getView().setModel(new JSONModel(res._content), "response")
            oView.getView().getModel("response").refresh();
            oView.loadingOff(oView)
          }
        });
      },

      loadResponseDataFiltered(oView, startDate, endDate) {
        $.ajax({
          url: `${baseUrl}/payment/dda?initialDate=${startDate}&finalDate=${endDate}`,
          type: 'GET',
          success: function (res) {
            oView.getView().setModel(new JSONModel(res._content), "response")
            oView.getView().getModel("response").refresh();
            oView.loadingOff(oView)
          }
        });
      },

      loadResponsePayedBankslips(oView) {
        $.ajax({
          url: `${baseUrl}/payment/dda?titleSituation=PAID`,
          type: 'GET',
          success: function (res) {
            oView.getView().setModel(new JSONModel(res._content), "responsePaid")
            oView.getView().getModel("responsePaid").refresh();
            oView.loadingOff(oView)
          }
        });
      },

      showHideBalance(oEvent) {
        var currentVisibility = this.getView().getModel('balanceVisible').oData.isVisible
        this.getView().setModel(new JSONModel({ isVisible: !currentVisibility }), "balanceVisible");
        var newIcon = currentVisibility == false ? 'sap-icon://show' : 'sap-icon://hide';
        oEvent.getSource().setIcon(newIcon)
      },

      // Passar motivo de bug May
      updatePaymentBadge(oEvent) {
        var bankslipsSelected = oEvent.getSource()._aSelectedPaths.length
        this.getView().setModel(new JSONModel({ quantity: oEvent.getSource()._aSelectedPaths.length }), "bankslips")
        var currentAmount = 0
        if (bankslipsSelected > 0) {
          oEvent.getSource()._aSelectedPaths.forEach(element => {
            currentAmount += this.getView().getModel('response').oData[element.substring(1, element.length)].nominalValue
          });
        }
        this.getView().setModel(new JSONModel({ totalAmount: currentAmount }), "paymentAmount")
      },

      addItemToPay(item) {
        if (listToShowReceipts[item.code]) {
          delete listToShowReceipts[item.code]
        } else {
          listToShowReceipts[item.code] = item
        }
      },

      addBarcordToPayList(code) {
        if (barcodesToPay[code]) {
          delete barcodesToPay[code]
        } else {
          barcodesToPay[code] = true
        }
      },

      getBalance(oView) {
        $.ajax({
          url: `${baseUrl}/account/balance`,
          type: 'GET',
          success: function (res) {
            oView.getView().setModel(new JSONModel(res), "balance")
          }
        });
      },

      onPayBankslipsPress: function (oEvent) {
        oEvent.getSource().oParent.oParent._aSelectedPaths.forEach(element => {
          var model = this.getView().getModel('response').oData[element.substring(1, element.length)]
          this.addItemToPay(model)
          this.addBarcordToPayList(model.code)
        });
        this.createPaymentDialog()
        this.oConfirmDialog.open();
      },

      createPaymentDialog() {
        this.oConfirmDialog = new Dialog({
          type: DialogType.Message,
          title: "Confirmar pagamento",
          content: [
            new HorizontalLayout({
              content: [
                new VerticalLayout({
                  width: "120px",
                  content: [
                    new Text({ text: "Data: " }),
                    new Text({ text: "Quantidade: " }),
                    new Text({ text: "Valor total: " })
                  ]
                }),
                new VerticalLayout({
                  content: [
                    new Text({ text: new Date(Date.now()).toLocaleString() }),
                    new Text({ text: this.getView().getModel('bankslips').oData.quantity }),
                    new Text({ text: this.formatDoubleToBRL(this.getView().getModel('paymentAmount').oData.totalAmount) })
                  ]
                })
              ]
            })
          ],
          beginButton: new Button({
            type: ButtonType.Emphasized,
            text: "Pagar",
            press: function () {
              this.oConfirmDialog.close();
              this.payBoleto(this)
              this.createBusyDialog()
            }.bind(this)
          }),
          endButton: new Button({
            text: "Cancelar",
            press: function () {
              this.oConfirmDialog.close();
            }.bind(this)
          })
        });
      },

      payBoleto(oView) {
        var postDataArray = []
        Object.keys(barcodesToPay).forEach(x => {
          postDataArray.push({ "barCode": x })
        })
        $.ajax({
          url: `${baseUrl}/payment/batch`,
          type: 'POST',
          contentType: 'application/json',
          data: JSON.stringify(postDataArray),
          success: function (res) {
            oView.getView().setModel(new JSONModel(res), "batchResponse")
            oView.loadingOff(oView)
            oView.resolveReturnedBatch()
          }
        });
      },

      resolveReturnedBatch() {
        var quantityOfBankslipsPayed = 0
              
        this.getView().getModel("batchResponse").oData.forEach(element => {
          if(element.status == 'PAYED') {
            quantityOfBankslipsPayed++;
          }
        })

        if(quantityOfBankslipsPayed == 0) {
          this.errorPayment()
        } else if(quantityOfBankslipsPayed < Object.keys(barcodesToPay).length) {
          this.partialPayment()
        } else {
          this.successPayment()
        }

        barcodesToPay = {}
        this.getView().setModel(new JSONModel({ quantity: 0 }), "bankslips")
        this.getView().setModel(new JSONModel({ totalAmount: 0 }), "paymentAmount")
      },

      successPayment() {
        MessageBox.success("Pagamentos realizados com sucesso.", {
          title: "Sucesso!"
        });
      },

      errorPayment() {
        MessageBox.error("Favor tentar novamente.", {
          title: "Erro!"
        });
      },

      partialPayment() {
        MessageBox.warning("Pagamentos realizados parcialmente, atualize sua tela e tente novamente.", {
          title: "Aviso!"
        });
      },

      createBusyDialog() {
        this.oInfoMessageDialog = new Dialog({
          type: DialogType.Message,
          title: "Processando pagamento",
          state: ValueState.Information,
          content: new BusyIndicator(),
          beginButton: new Button({
            type: ButtonType.Emphasized,
            text: "Fechar",
            press: function () {
              this.oInfoMessageDialog.close()
            }.bind(this)
          }),
        })
        this.oInfoMessageDialog.open()
        this.getView().setModel(new JSONModel(list), "batchResponse")
        this.getView().setModel(new JSONModel({ quantity: 0 }), "bankslips")
      },

      getReceiptByBarcode(oEvent) {
        var bc = oEvent.getSource().getAttributes()[0].getText()
        var barcode = bc.replaceAll(".", "")

        $.ajax({
          url: `${baseUrl}/payment/${barcode}`,
          type: 'GET',
          success: function (res) {
            oEvent.getView().setModel(new JSONModel(res._content), "singleReceipt")
            oEvent.getView().getModel("singleReceipt").refresh();
            oEvent.loadingOff(oEvent)
            singleReceiptsDialogShow()
          }
        });
        oEvent.getView().setModel(new JSONModel(res._content), "singleReceipt")
        oEvent.getView().getModel("singleReceipt").refresh();
        oEvent.loadingOff(oEvent)
        this.singleReceiptsDialogShow()
      },

      updatePayed() {
        this.loadResponsePayedBankslips(this)
      },

      formatDoubleToBRL(number) {
        return number.toLocaleString('pt-br', { style: 'currency', currency: 'BRL' });
      }
    });
  }
);
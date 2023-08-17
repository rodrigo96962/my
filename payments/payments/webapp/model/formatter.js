sap.ui.define([], function () {
	"use strict"
	return {
		cnpj: function (number) {
			if (number != null) {
				return '' + number.replace(/\D/g, '')
					.replace(/(\d{2})(\d)/, '$1.$2')
					.replace(/(\d{3})(\d)/, '$1.$2')
					.replace(/(\d{3})(\d)/, '$1/$2')
					.replace(/(\d{4})(\d)/, '$1-$2')
					.replace(/(-\d{2})\d+?$/, '$1')
			} else {
				return number
			}
		},

		barcode: function (barcodeText) {
			if (barcodeText != null) {
				return barcodeText.substring(0, 5) + '.'
					 + barcodeText.substring(5, 10) + '.' 
					 + barcodeText.substring(10, 15) + '.' 
					 + barcodeText.substring(15, 21) + '.' 
					 + barcodeText.substring(21, 32) + '.' 
					 + barcodeText.charAt(32) + '.' 
					 + barcodeText.substring(33, 48)
			} else {
				return barcodeText
			}
		},

		statusEnumConverter: function (oStatus) {
			switch (oStatus) {
				case 'PAID':
					return 'Pago'
					break;
				case 'OPEN_EXPIRED':
					return 'Vencido'
					break;
				case 'OPEN_TO_EXPIRE':
					return 'Em aberto'
					break;
				default:
					return 'Fechado'
					break;
			}
		},

		statusState: function(oStatus) {
			switch (oStatus) {
				case 'PAID':
					return 'Success'
					break;
				case 'OPEN_EXPIRED':
					return 'Error'
					break;
				case 'OPEN_TO_EXPIRE':
					return 'Warning'
					break;
				default:
					return 'Information'
					break;
			}
		},

		date: function(oDate) {
			var newDate = new Date(oDate.toString())
			return `${newDate.getDay()}/${newDate.getMonth()}/${newDate.getFullYear()}`
		}
	}
})
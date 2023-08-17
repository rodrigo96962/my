/*global QUnit*/

sap.ui.define([
	"payments/controller/DDA.controller"
], function (Controller) {
	"use strict";

	QUnit.module("DDA Controller");

	QUnit.test("I should test the DDA controller", function (assert) {
		var oAppController = new Controller();
		oAppController.onInit();
		assert.ok(oAppController);
	});

});

package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductInventoryStatus {
  OUT_OF_STOCK("Sem estoque"),
  IN_STOCK("Com estoque");

  private final String prettyName;

}
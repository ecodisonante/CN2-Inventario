#!/bin/bash
set -e

# Variables
RESOURCE_GROUP=inventario-RG
LOCATION=eastus2
TOPIC_NAME=inventory-topic
STORAGE_NAME=invstor$(date +%s)   # nombre único para dead-letter

# Crear RG
az group create --name $RESOURCE_GROUP --location $LOCATION

# Crear Storage (para dead-letter)
# az storage account create \
#   --name $STORAGE_NAME \
#   --location $LOCATION \
#   --resource-group $RESOURCE_GROUP \
#   --sku Standard_LRS

# Crear Event Grid Topic
# az eventgrid topic create \
#   --name $TOPIC_NAME \
#   --location $LOCATION \
#   --resource-group $RESOURCE_GROUP

# Obtener endpoint y key
ENDPOINT=$(az eventgrid topic show \
  --name $TOPIC_NAME \
  --resource-group $RESOURCE_GROUP \
  --query "endpoint" -o tsv)

KEY=$(az eventgrid topic key list \
  --name $TOPIC_NAME \
  --resource-group $RESOURCE_GROUP \
  --query "key1" -o tsv)

# echo "Event Grid Topic endpoint: $ENDPOINT"
# echo "Event Grid Topic key: $KEY"


#########################################
# Crear suscripción a Azure Function
#########################################
az eventgrid event-subscription create \
  --name notify-mail-sub \
  --source-resource-id $(az eventgrid topic show --name $TOPIC_NAME --resource-group $RESOURCE_GROUP --query id -o tsv) \
  --endpoint "<URL_HTTPS_DE_TU_FUNCTION>" \
  --included-event-types Product.Created Product.Updated Product.Deleted Warehouse.Created Warehouse.Updated Warehouse.Deleted Inventory.StockReceived


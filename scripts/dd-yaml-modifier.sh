#!/bin/bash

# Function to create the conf.d/java.d/conf.yaml file
create_conf_yaml() {
CONF_YAML_CONTENT=$(cat <<EOL
# Log section
logs:
  # - type : file (mandatory) type of log input source (tcp / udp / file)
  #   port / path : (mandatory) Set port if type is tcp or udp. Set path if type is file
  #   service : (mandatory) name of the service owning the log
  #   source : (mandatory) attribute that defines which integration is sending the log
  #   sourcecategory : (optional) Multiple value attribute. Can be used to refine the source attribute
  #   tags: (optional) add tags to each log collected

  - type: file
    path: /opt/$folder_name/logs/app.log
    service: $folder_name
    source: java
    sourcecategory: sourcecode
    # For multiline logs, if they start with a timestamp with format yyyy-mm-dd uncomment the below processing rule
    # log_processing_rules:
    #   - type: multi_line
    #     pattern: \d{4}\-(0?[1-9]|1[012])\-(0?[1-9]|[12][0-9]|3[01])
    #     name: new_log_start_with_date
EOL
)
  # Create the conf.d/java.d directory if it doesn't exist
  mkdir -p /etc/datadog-agent/conf.d/java.d

  # Write the content to the conf.yaml file and replace FOLDER_PATH_PLACEHOLDER and FOLDER_NAME_PLACEHOLDER
  # with the actual folder path and name using sed
  echo "$CONF_YAML_CONTENT" | sed "s@FOLDER_PATH_PLACEHOLDER@$folder_path@g; s@FOLDER_NAME_PLACEHOLDER@$folder_name@g" > /etc/datadog-agent/conf.d/java.d/conf.yaml

  # Inform the user that the file has been created
  echo "The conf.d/java.d/conf.yaml file has been created with the provided content."
}

# Function to create the conf.d/process.d/conf.yaml file
create_process_conf_yaml() {
CONF_YAML_CONTENT=$(cat <<EOL
init_config:

instances:
    - name: java
      search_string: ['java']
EOL
)
  # Create the conf.d/java.d directory if it doesn't exist
  mkdir -p /etc/datadog-agent/conf.d/process.d

  # Write the content to the conf.yaml file
  echo "$CONF_YAML_CONTENT" > /etc/datadog-agent/conf.d/process.d/conf.yaml

  # Inform the user that the file has been created
  echo "The conf.d/process.d/conf.yaml file has been created with the provided content."
}

# Function to update the datadog.yaml file's logs_enabled setting to true
update_dd_yaml() {
  sed -i.bak 's@# logs_enabled: false@logs_enabled: true@' /etc/datadog-agent/datadog.yaml
  rm /etc/datadog-agent/datadog.yaml.bak
}

# Use 'find' command to search for folders with names containing "-service"
# The '-type d' option specifies to look for directories only
# The '-name' option with '*-service*' specifies the pattern to match in folder names
# The '-print' option prints the matching folder names
folder_path=$(find /home/ec2-user -maxdepth 1 -type d -name "*-service" -print -quit | sed 's|^\./||')
folder_name=$(basename "$folder_path")

# Check if any folder with '-service' in its name is found
if [ -n "$folder_name" ]; then
  echo "Found folders with '-service' in their names."
  update_dd_yaml
  echo "Updated dd.yaml with 'logs_enabled: true'."
  # Call the function to create the java/conf.yaml file
  create_conf_yaml
  # Call the function to create the process/conf.yaml file
  create_process_conf_yaml
  # Restart Datadog
  sudo systemctl restart datadog-agent
else
  echo "No folders with '-service' in their names found."
fi
FROM 1science/sbt
COPY . /app
RUN chmod +x run_all_vizgen_cron.sh

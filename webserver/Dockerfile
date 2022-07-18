FROM node:14
WORKDIR /usr/src/app
COPY . .
RUN npm install
EXPOSE 3000
CMD [ "mv", "env.example .env" ]
CMD [ "npm", "start" ]
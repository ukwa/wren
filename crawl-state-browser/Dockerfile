FROM ruby:2.3.0
RUN apt-get update -qq && apt-get install -y build-essential libpq-dev nodejs
RUN mkdir /blacklight
WORKDIR /blacklight
ADD Gemfile /blacklight/Gemfile
ADD Gemfile.lock /blacklight/Gemfile.lock
RUN bundle install
ADD . /blacklight

CMD bundle exec rails s -p 3000 -b '0.0.0.0'

